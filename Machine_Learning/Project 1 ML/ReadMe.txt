1) Naive Bayes Classification:

   There exist a Target Value Set and many Feature Sets. After all data have been analized,
   the probability of each Target Value from its set must be found. Then for every Target 
   Value find the probability for each feature for all sets. Once all the features
   probabilities have been found within their target value, then you can find the missing
   target value that approximates to a set of features.

   Algorithm :
  
   1) Find the number of Features Sets that will be used
   2) For every target value, store their combination of features in their corresponding sets
   3) After all data has been stored, iterate through every target value and find their
      probability and the probability for each of its features.
   4) Now using testing data, find the probability of the combination of features for 
      each target value.
   5) Compare all probabilities for each target value and the target value with the highest
      probability could be the target value.


2) To compile type : "javac NaiveBayes.java"

3) To Run          : "java NaiveBayes training.txt testing.txt predictions.txt"