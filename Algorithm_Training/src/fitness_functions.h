#ifndef FITNESS_FUNCTIONS_H_INCLUDED
#define FITNESS_FUNCTIONS_H_INCLUDED

/* Maximum UPDRS rating found in the sheet */
#define MAX_UPDRS 56
/* CGP parameters to be parsed into the program */
#define CGP_PARAMS 12

/* Initial values for threshold classifier */
extern double threshold;
extern double threshIncre;
extern double classNumber;

/* Function prototypes */
double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
double fourOutputFitnessFunction(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
double totalSum(struct parameters *params, struct chromosome *chromo, struct dataSet *data);

int maxIndex(double *arr);
char **importCGPParams(void);
int getBestEntity(void);

void stcAction(struct chromosome *chromo, struct dataSet *testData);
void ftcAction(struct chromosome *chromo, struct dataSet *testData);
void tsAction(struct chromosome *chromo, struct dataSet *testData);
void setDisplayAction(char *arr, struct chromosome *chromo, struct dataSet *testData);
void setFitnessFromText(char *arr, struct parameters *params);


#endif // FITNESS_FUNCTIONS_H_INCLUDED
