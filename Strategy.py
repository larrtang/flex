from abc import ABC, abstractmethod 

class Strategy(ABC):

    def __init__(self, executor):
        
        #executer controls orders 
        self.executor = executor
        


    #handles data on each tick
    def onTick(self):
        pass

    
    