from AlphaVantage import getIntradayData

class engine:
    
    def __init__ (self, backtest = 0, trader):
        self.backtest = backtest
        self.timestamps = None
        self.market_data = {}
        self.sym_list = []  # get from Strategy
        self.trader = trader

    
    
    def begin(self):
        
        #main loop
        while True:
            self.getLatestData()
            pass


    def getLatestData(self):
        
        #get timestamps
        self.timestamps, _ = getIntradayData()

        #get individual symbol's market data
        for sym in self.sym_list:
            _, data = getIntradayData(sym)
            self.market_data[sym] = data
        
        

