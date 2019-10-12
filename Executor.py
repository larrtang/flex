
'''
Represents a mock broker during backtesting
'''
class BTExecutor:

    def __init__ (self, capital=10000):
        self.intial_capital = capital
        self.capital = capital
        self.buying_power = capital
        self.positions = {'cash':capital}

    def market_buy(self, sym='SPY', quantity=1, current_price):
        if sym in self.positions:
            self.positions[sym] += quantity
        else self.positions[sym] = quantity

        self.buying_power -= current_price * quantity
    
    def market_sell(self, sym='SPY', quantity=1, current_price):
        if sym in self.positions:
            self.positions[sym] -= quantity
        else self.positions[sym] = -quantity

        self.buying_power += current_price * quantity
    
    def getPortfolioValue(self):
        pass