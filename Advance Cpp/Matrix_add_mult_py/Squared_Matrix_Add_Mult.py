#Matrix addition - Must be squared matrixes
def add(a,b):
    #Squared matrixes
    la = len(a)
    lb = len(b)
    if la < 1:
        print("Nothing to be done")
        return
    if la != lb:
        print("Matrix must have the same size")
        return
    c = [ [i] * la for i in range(la)]
    #Create a matrix for non-squared matrixes
    #c = []
    #for i in range(la):
    #    x = []
    #    for j in range(len(a[i])):
    #        x.append([0])
    #    c.append(x)                 
    for i in range(la):
        for j in range(len(a[i])):
            c[i][j] = a[i][j] + b[i][j]
    return c

#Matrix multiplication - must be squared matrixes
def mult(a,b):
    la = len(a)
    lb = len(b)
    if la < 1:
        print("Nothing to be done")
        return
    if la != lb and la != len(a[0]):
        print("Matrixes must have squared size")
        return
    c = [ [0 for c in range(la)] for r in range(la) ]
    _lb = len(b[0])
    for row in range(la):
        for col in range(_lb):
            for k in range(lb):
                c[row][col] += a[row][k] * b[k][col]
    return c

a = [ [1, 2, 9], [3, 4, 10], [5, 6, 0] ]
b = [ [2, 3, 11], [4, 5, 25], [6, 7, 30] ]
print("Elements")
print(a)
print(b)
w = add(a,b)
z = mult(a,b)
print("Addition")
print(w)
print("Multiplication")
print(z)
