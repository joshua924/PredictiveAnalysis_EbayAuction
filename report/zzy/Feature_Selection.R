# Load package
library(FSelector)

# Read in data
data<-read.csv("/Users/zhaoyinzhu/Dropbox/NYU/GA 3033 Predictive Analysis/Project/PredictiveAnalysis_EbayAuction/data/PreprocessedData.csv")
sub.data<-subset(data,select=-c(2,3))

# Entropy based filter
weight_entropy <- information.gain(QuantitySold~., sub.data)
print(weight_entropy)
subset_entropy <- cutoff.k(weight_entropy, 7)
print(subset_entropy)


# Chi-square based filter
weight_chi <- chi.squared(QuantitySold~., sub.data)
print(weight_chi)
subset_chi<- cutoff.k(weight_chi, 7)
print(subset_chi)

# Correlation based filter
weight_cor<-linear.correlation(QuantitySold~., sub.data)
print(weight_cor)
subset_cor<-cutoff.k(weight_cor,7)
print(subset_cor)

