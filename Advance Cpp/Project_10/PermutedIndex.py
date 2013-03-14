from collections import defaultdict

def populateData(inputFile):
    #Init variables
    dictionary = {}  
    inputF = open( inputFile,'r' )
    pos = 1
    
    #Populate the dictionary
    for line in inputF:
        line = line.rstrip()
        words = line.split(' ')
        for i in range( len(words) ):
            if ( words[ i ] in dictionary ):
                dictionary[ words[ i ] ][ pos ] += 1
            else:
                #Skip new line characters
                #if i == '\n':
                #    continue
                dictionary[ words[ i ] ] = defaultdict(int)
                dictionary[ words[ i ] ][ pos ] = 1
        pos += 1
    inputF.close()
    return dictionary

def writeData(dictionary, output):
    outputF = open( outputFile,'w' )
    buffer = ''
    #Iterate through the dictionary
    for i in sorted(dictionary.keys()):
        buffer += i + ':'
        index = 1;
        for line in sorted( dictionary[i].keys() ):
            buffer += ' ' + str(line)

            #If there are more elements in the same line then display
            if( dictionary[i][line] > 1 ):
               buffer += '(' + str( dictionary[i][line] )+')'
            #Add the Comma
            if( index < len( dictionary[i ] ) ):
               buffer += ','
               
            index += 1
            
        outputF.write( buffer + '\n' )
        buffer = ""
    outputF.close()

#Start
inputFile = input('Enter name for input file : ')
outputFile = input('Enter name for output file : ')

dictionary = populateData(inputFile)
writeData(dictionary,outputFile)
