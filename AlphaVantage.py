import requests
import os
import pandas as pd
from MarketDataWrapper import MarketDataWrapper

def getIntradayData(sym='SPY'):
    api_key=os.getenv('AV')
    url = 'https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol='+sym+'&interval=5min&outputsize=full&apikey='+api_key
    response = requests.get(url)
    data = response.json()["Time Series (5min)"]
    #print(data)
    df = pd.DataFrame(data)
    timestamps = list(df.columns)
    timestamps.reverse()

    #print(df.index)
    #print(list(df.columns))     # go from closest to farthest away
    #print(df['2019-07-10 16:00:00']['4. close'])
    #print(df['4. close'])
    return timestamps, df


def getDailyData(sym='SPY'):
    ## TODO: 
    api_key=os.getenv('AV')
    url = 'https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol='+sym+'&outputsize=full&apikey='+api_key
    response = requests.get(url)
    #print(response.json())
    data = response.json()['Time Series (Daily)']
    #print(data)
    df = pd.DataFrame(data)
    timestamps = list(df.columns)
    timestamps.reverse()

    #print(df.index)
    #print(list(df.columns))     # go from closest to farthest away
    #print(df['2019-07-10 16:00:00']['4. close'])
    #print(df['4. close'])
    return timestamps, df

    return None



def parseData(data, limit=99999):

    o = []
    h = []
    l = []
    c = []
    v = []

    i = 0
    for k in data:
        o.append(float(data[k]['1. open']))
        h.append(float(data[k]['2. high']))
        l.append(float(data[k]['3. low']))
        c.append(float(data[k]['4. close']))
        v.append(float(data[k]['5. volume']))
        i += 1
        if i > limit: break

    
    o.reverse()
    h.reverse()
    l.reverse()
    c.reverse()
    v.reverse()
    
    mktData = MarketDataWrapper(o,h,l,c,v)
    return mktData



if __name__ == '__main__':
    time, df = getDailyData()
    for i in df:
        print(df[i])