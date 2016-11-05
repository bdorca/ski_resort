import time

class Lift:
    def __init__(self):
        self.queue = 0
        self.speed = 0 # People/min
        self.current_transition = 0
        self.resource = 0
        self.consumption = 0
        self.last_sim_time = 0

    def simulation(self):
        self.last_sim_time = time.time()
        while True:
            t = time.time() - self.last_sim_time
            self.simulate(t)
            self.last_sim_time = time.time()


    def simulate(self,t):
        if self.resource > self.consumpion:
            self.resource -= self.consumption
            self.current_transition = (self.speed / 60.0) * t
            if self.current_transition > 1:
                self.queue -= int(self.current_transition)
                self.current_transition -= int(self.current_transition)

    def add_people(self,number):
        self.queue += number

    def change_speed(self,new_speed):
        self.speed = new_speed
