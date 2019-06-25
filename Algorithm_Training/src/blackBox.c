#include <stdio.h>
#include "fitness_functions.h"

double threshIncre;
double threshold;
double classNumber;


int main(void)
{
    char **controlDataSet = importFile("controlConsole.csv",53);
    char *splitStr = strSplit(controlDataSet[0],",",1);
    printf("%s",splitStr);
}
