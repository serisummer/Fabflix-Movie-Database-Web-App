with open('log.txt', 'r') as file:
    sumTS = 0
    sumJS = 0
    for line in file:
        line = line.strip().split(',')
        ts = line[0]
        js = line[1]
        sumTS += int(ts)
        sumJS += int(js)
    print(sumTS, sumJS)