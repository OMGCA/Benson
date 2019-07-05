#ifndef FITNESS_FUNCTIONS_H_INCLUDED
#define FITNESS_FUNCTIONS_H_INCLUDED

/* Maximum UPDRS rating found in the sheet */
#define MAX_UPDRS 56
/* CGP parameters to be parsed into the program */
#define CGP_PARAMS 10

/* Initial values for threshold classifier */
extern double threshold;
extern double threshIncre;
extern double classNumber;

/* Function prototypes */
double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
double fourOutputFitnessFunction(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
double totalSum(struct parameters *params, struct chromosome *chromo, struct dataSet *data);

int maxIndex(double *arr);

int getBestEntity(char* randomNum);
double doubleMax(double a, double b);
double doubleMin(double a, double b);

void stcAction(struct chromosome *chromo, struct dataSet *testData);
void ftcAction(struct chromosome *chromo, struct dataSet *testData);
void tsAction(struct chromosome *chromo, struct dataSet *testData);
void setDisplayAction(char *arr, struct chromosome *chromo, struct dataSet *testData);
void setFitnessFromText(char *arr, struct parameters *params);

void runKFold(struct parameters *params, int numGens, int kFoldVar, char *fitnessFunction, char *randomNum);

double* softmax(double arr[], int arrLength);
double stcConfidence(int thresholdMargin[], double output);
void pdDecode(int index);
int stcOutputDecode(double chromoOutput);
char **importFile(char *fileName);
char *strSplit(char *strArr, char *delimiter, int index);

void insertionSort(double *arr, char **idArr, int length);
int binarySearch(double *arr, int arrSize, double target);

#endif // FITNESS_FUNCTIONS_H_INCLUDED
