import requests
import os
import pandas as pd


def getData(sym='BTC-USD', granularity=300):
    '''
    granularities:
    num seconds
    60 - 1 min
    300 - 5 min
    ...

    '''
    api_key=os.getenv('AV')
    url = 'https://api.pro.coinbase.com/products/'+sym+'/candles?granularity='+str(granularity)
    response = requests.get(url)
    data = response.json()
    print(data[0])
    #print(data)
    df = pd.DataFrame(data)
    print(df)
    print(df.index)
    #print(list(df.columns))     # go from closest to farthest away
    #print(df['2019-07-10 16:00:00']['4. close'])
    #print(df['4. close'])
    return list(df.columns), df


if __name__ == '__main__':
    time, df = getData()
    #for i in df:
    #    print(df[i])