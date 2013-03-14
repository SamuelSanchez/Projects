from fractions import gcd

class rat:
    def __init__( self,n = 0, d = 1 ):
        self._n = n
        if d==0:
            self._n = 0
            self._d = 1
        else:
            self._d = d

    #Addition operators
    def __add__( self,other ):
        if type(other)==type(1):
            num = self._n + other * self._d
            den = self._d
            f = gcd(num,den)
            num = num//f
            den = den//f
            return rat(num,den)
            # alternatively, we can just say
            # if type(self)==type(1):
            # return self +rat(other)
        else:
            num=self._n*other._d+self._d*other._n
            den=self._d*other._d
            f=gcd(num,den)
            num=num//f
            den=den//f
            return rat(num,den)
            
    def __radd__(self,other):
        if type(other)==type(1):
            return rat(self._n+other*self._d,self._d)
        else:
            return rat(self._n*other._d+self._d*other._n,self._d*other._d)

    #Substraction operators
    def __sub__(self,other):
        if type(other)==type(1):
            num = self._n - other * self._d
            den = self._d
            f = gcd(num,den)
            num = num//f
            den = den//f
            return rat(num,den)
        else:
            num=self._n*other._d-self._d*other._n
            den=self._d*other._d
            f=gcd(num,den)
            num=num//f
            den=den//f
            return rat(num,den)
    
    def __rsub__(self,other):
        if type(other)==type(1):
            return rat(other*self._d-self._n,self._d)
        else:
            return rat(self._n*other._d-self._d*other._n,self._d*other._d)

    #Multiplication Operators
    def __mul__(self,other):
        if type(other)==type(1):
            num = self._n*other
            den = self._d
            f = gcd(num,den)
            num = num//f
            den = den//f
            return rat(num,den)
        else:
            num=self._n*other._n
            den=self._d*other._d
            f=gcd(num,den)
            num=num//f
            den=den//f
            return rat(num,den)
            
    def __rmul__(self,other):
        if type(other)==type(1):
            return rat(self._n*other,self._d)
        else:
            return rat(self._n*other._n,self._d*other._d)
        
    #Division operators
    def __floordiv__(self,other):
        if type(other)==type(1):
            num = self._n
            den = self._d*other
            f = gcd(num,den)
            num = num//f
            den = den//f
            return rat(num,den)
        else:
            num=self._d*other._n
            den=self._n*other._d
            f=gcd(num,den)
            num=num//f
            den=den//f
            return rat(num,den)
            
    def __rfloordiv__(self,other):
        if type(other)==type(1):
            return rat(other*self._d,self._n)
        else:
            return rat(self._n*other._d,self._d*other._n)
            
    #Other Operators

    def __str__(self):
        if self._n == 0:
            return '0'
        if self._d == 1:
            return str(self.n)
        #Get the whole number if there are any
        if abs(self._n) >= self._d:
            sign = ""
            if(self._n < 0):
                sign = "-"
            _r_ = abs(self.n) % self.d
            _n_ = abs(self._n) // gcd(self._n,self._d)
            _d_ = self._d // gcd(abs(self._n),self._d)
            _u_ = _n_ // _d_
            if _r_ == 0:
                return sign + str(_u_)
            else:
                _D_ = gcd(_r_,self._d)
                return sign + str(_u_) + ' ' + str(_r_//_D_) + '/' + str(self._d//_D_)
        _D_ = gcd(self._n,self._d)
        return str( self._n//_D_ ) + '/' + str( self._d//_D_ )


    def __repr__(self):
        return str(self)

    def getn(self):
        return self._n

    def getd(self):
        return self._d

    def setn(self,n):
        self._n=n

    def setd(self,d):
        self._d=d
    # the python way using decorators for getters

    @property
    def n(self):
        return self._n

    @property
    def d(self):
        return self._d

    # the python way to use properties for setters
    @n.setter
    def n(self,other):
        self._n=other

    @d.setter
    def d(self,other):
        self._d=other
    # now we can use “rat” with the functionality defined in the class above

    
#-------------------------
def main():
    a = rat(2)
    b = rat(3,10)
    c = rat(10,3)
    d = rat(-11,3)
    e = a + b
    f = c - d
    g = a * d
    h = d // a
    print("a : ")
    print(a)
    print('\n')
    print("b : " )
    print(b)
    print('\n')
    print("c : " )
    print(c)
    print('\n')
    print("d : " )
    print(d)
    print('\n')
    print("a+b : " )
    print(e)
    print('\n')
    print("c-d : " )
    print(f)
    print('\n')
    print("a*d : " )
    print(g)
    print('\n')
    print("d//a : " )
    print(h)
    #w = rat(2)
    #x = rat(1)
    #z = w // x
    #print(w)

if __name__ == "__main__":
    main()
