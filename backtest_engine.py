
from AlphaVantage import getIntradayData, parseData, getDailyData
#from matplotlib.finance import candlestick2_ohlc
from mpl_finance import candlestick2_ohlc
import matplotlib.pyplot as plt
#import talib
import sys


def vwap_period(prices, volumes):
    assert len(prices) == len(volumes)
    vwap = []
    for i in range(len(prices)):
        pv = [prices[j]*volumes[j] for j in range(i)]
        try:
            p = sum(pv) / sum(volumes[:i])
            #print(p)
            vwap.append( p )
        except:     # divide by zero
            vwap.append(prices[i])           
            #print(prices[i]) 
    return vwap
    #return (prices * volumes).sum() / volumes.sum()


def vwap_moving(prices, volumes, window_length=14):
    assert len(prices) == len(volumes)
    vwap = []
    for i in range(len(prices)):
        if i > window_length:
            start = i - window_length
        else:
            start = 0
        #print (start)
        pv = [prices[j]*volumes[j] for j in range(start, i, 1)]
        try:
            p = sum(pv) / sum(volumes[start:i])
            #print(p)
            vwap.append( p )
        except:     # divide by zero
            vwap.append(prices[i])           
            #print(prices[i]) 
    return vwap
    #return (prices * volumes).sum() / volumes.sum()  



## Doesn't work with holidays
def vwap_daily(prices, volumes):
    numperday = 78
    assert len(prices) == len(volumes)
    vwap = []
    for i in range(len(prices)):
        start = int(i/numperday) * numperday
        pv = [prices[j]*volumes[j] for j in range(start, i, 1)]
        try:
            p = sum(pv) / sum(volumes[start:i])
            #print(p)
            vwap.append( p )
        except:     # divide by zero
            vwap.append(prices[i])           
            #print(prices[i]) 
    return vwap








class engine:
    
    '''
    start, end --- timestamps from beginning to end of backtest
    '''
    def __init__ (self, backtest = 1, start=0, end=0, symbol='SPY', intraday=True):
        self.backtest = backtest
        self.start = start
        self.current = start
        self.end = end
        if intraday:
            self.timestamps, self.data = getDailyData(symbol)
        else:
            self.timestamps, self.data = getDailyData(symbol)
    


    '''
    shows UI for visual analysis 
    '''
    def analysis(self):
        o,h,l,c,v = parseData(self.data)#, limit=78)
        #print(v)
        #upper, middle, lower = talib.BBANDS(c, 20, 2, 2)
        vwap = vwap_period(c, v)
        vwma = vwap_moving(c, v, window_length=12)

        fig = plt.figure()
        ax1 = fig.add_subplot(311)
        ax1.plot(c)
        ax1.plot(vwap, color='orange')
        ax1.plot(vwma, color='red')
        ax1.grid(True)
        #candlestick2_ohlc(ax1,o,h,l,c, width=0.4, colorup='green', colordown='red')
        


        ax2 = fig.add_subplot(312)
        ax2.bar([i for i in range(len(v))], v)


        mean_deviation = [c[i] - vwma[i] for i in range(len(vwma))]
        ax3 = fig.add_subplot(313)
        ax3.plot(mean_deviation)

        plt.show()



    def __stepData(self, i,o,h,l,c,v):
        return o[i], h[i], l[i], c[i], v[i], self.timestamps[i]



    def begin(self):
        O,H,L,C,V = parseData(self.data)
        n = len(C)
        #main loop
        for i in range(n):
            o,h,l,c,v, time = self.__stepData(i,O,H,L,C,V)
            print(time, c)
        #while self.current < self.end:
        #    pass

        self.analysis()

    
    
if __name__ == '__main__':
    backtest = engine()
    backtest.begin()

