import functools
import json
import asyncio
import random
import time
import enum
import logging
import multiprocessing
import sys
import websockets
import requests


class LiftType(enum.Enum):
    small = 1
    medium = 2
    large = 3
    extra_large = 4


class Lift:
    MAX_SPEED = {LiftType.small: 60,
                 LiftType.medium: 75,
                 LiftType.large: 90,
                 LiftType.extra_large: 120
                 }

    def __init__(self, type, my_id="", name="", queue=0, speed=0, resource=0, consumption=0):
        self.input = multiprocessing.SimpleQueue()
        self.output = multiprocessing.SimpleQueue()
        self.id = my_id
        self.name = name
        self.queue = queue
        self.speed = speed  # People/min
        self.current_transition = 0
        self.resource = resource  # Fixed amount that the system can provide
        self.consumption = consumption  # Fixed amount, that the lift requires to maintain speed
        self.last_sim_time = 0
        self.type = type
        self.event_rate = {
            "failure": 0.05,
            "add_people": 1.5
        }
        self.running = False
        self.exit = False
        self.websocket_handler = Lift.WebSocketManager("localhost", 8080, "SkiServerWeb/lift", self.input, self.output)
        p = multiprocessing.Process(target=self.websocket_handler.start)
        p.start()

    def simulation(self):
        self.last_sim_time = time.time()
        self.running = True
        while not self.exit:
            if self.running:
                if not self.input.empty():
                    while not self.input.empty():
                        self.handle_command(self.input.get())

                t = time.time() - self.last_sim_time
                self.last_sim_time = time.time()
                if self.queue > 0:
                    self.simulate(t)
                self.enviromental_happening()
                time.sleep(0.05)

    def simulate(self, t):
        if self.resource >= self.consumption:
            self.current_transition += (self.speed / 60.0) * t
            if self.current_transition > 1:
                self.queue -= int(self.current_transition)
                print("Transported {} people, {} remaining. Time:{}".format(int(self.current_transition), self.queue,
                                                                            time.time() % 1000))
                self.current_transition -= int(self.current_transition)
        else:
            self.find_lowest_available_speed()

    def handle_command(self, command):
        list_of_commands = ["speed", "change_speed", "resource", "increased_pop", "decreased_pop", "customer", "report",
                            "online", "offline", "exit", "id", "name"]
        c, arg = (command["command"], command["arg"])

        if c not in list_of_commands:
            logging.error("Unknown command {}".format(command))
        else:
            if c == "speed":
                self.change_speed(float(arg))
            elif c == "change_speed":
                self.change_speed(float(arg), False)
            elif c == "customer":
                self.add_people(int(arg))
            elif c == "report":
                self.output.put(self.create_report())
            elif c == "exit":
                self.exit = True
                self.websocket_handler.exit = True
            elif c == "resource":
                self.resource = float(arg)
            elif c == "increased_pop":
                self.event_rate["add_people"] += int(arg)
            elif c == "decreased_pop":
                new_rate = self.event_rate["add_people"] - int(arg)
                self.event_rate["add_people"] = new_rate if new_rate >= 0 else 0
            elif c == "online":
                self.running = True
            elif c == "offline":
                self.running = False
            elif c == "id":
                self.id=str(arg)
            elif c == "name":
                self.name=str(arg)

    def create_report(self):
        rep = {
            "action":"report",
            "data":{
                "id": self.id,
                "name": self.name,
                "type": self.type.name,
                "speed": self.speed,
                "customers": self.queue,
                "resource": self.resource,
                "consumption": self.consumption,
                "events": self.event_rate
            }
        }
        print(rep)
        return json.dumps(rep)

    def add_people(self, number):
        self.queue += number

    def change_speed(self, new_speed, absolute=True):
        if not absolute:
            new_speed = self.speed + new_speed

        if new_speed <= self.MAX_SPEED[self.type]:
            self.speed = new_speed
        else:
            self.speed = self.MAX_SPEED[self.type]
        if self.speed < 0:
            self.speed = 0

        self.consumption = self.calculate_consumption(self.speed, self.type)

    def find_lowest_available_speed(self):
        for i in range(self.speed + 1):
            if self.calculate_consumption(self.speed - i,self.type) <= self.resource:
                self.change_speed(self.speed - i)
                break

    def enviromental_happening(self):

        l = []
        denom = 1.0 / min(self.event_rate.values())
        total = sum(self.event_rate.values())
        for key in self.event_rate:
            l += [key] * int(self.event_rate[key] * 20)
        if total < 100:
            l += ["nothing"] * int((100 - total) * 20)

        c = random.choice(l)

        if c == "failure":
            print("Server has unexpectedly stopped, what could have happened? :O Somebody needs to set it online.")
            self.running = False
        elif c == "add_people":
            self.queue += random.randint(0, 5)

    @staticmethod
    def calculate_consumption(speed, lift_type):
        base_consumption = 30  # kW/h/min
        if lift_type == LiftType.small:
            return base_consumption + pow(speed, 1.1)
        elif lift_type == LiftType.medium:
            return base_consumption + pow(speed, 1.2)
        elif lift_type == LiftType.large:
            return base_consumption + pow(speed, 1.3)
        else:
            return base_consumption + pow(speed, 1.35)

    class WebSocketManager:
        def __init__(self, hostname, port, target, input: multiprocessing.SimpleQueue, output: multiprocessing.SimpleQueue):
            self.hostname = hostname
            self.port = port
            self.target=target
            self.input = input
            self.output = output
            self.exit = False

        def start(self):
            asyncio.get_event_loop().run_until_complete(self.handler())

        # async def try_handler(self):
        #     async with websockets.connect("ws://localhost:8085") as websocket:
        #         print(await websocket.recv())
        #         await websocket.send("Hi, I'm Adam")
        #         print("Hi I'm Adam")
        #         print(await websocket.recv())
        #         for i in range(4):
        #             await websocket.send("WTF")
        #             data = await websocket.recv()
        #             print(data)
        #             self.channel.put(data)
        #
        #         await websocket.send("PLEASE")
        #         while not self.channel.empty():
        #             print(self.channel.get())


        async def handler(self):
            while not self.exit:
                try:
                    async with websockets.connect("ws://{}:{}/{}".format(self.hostname, self.port,self.target)) as websocket:

                        data = await websocket.recv()
                        command = json.loads(data)
                        while not command["command"] == "close" and not self.exit:
                            self.input.put(command)
                            if command["command"] == "report":
                                # while self.output.empty():
                                #     time.sleep(0.05)
                                await websocket.send(self.output.get())
                            data = await websocket.recv()
                            command = json.loads(data)

                except Exception as e:
                    logging.error("Connection lost to server. Retrying...")
                    # print(a)


def create_lift(web_url):
    rsp = requests.get(web_url)


def main():
    l = Lift(LiftType.small)
    print("asd")
    l.queue = 50
    l.change_speed(60)
    l.resource = 50000

    l.simulation()


if __name__ == '__main__':
    main()
