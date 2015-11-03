library(boot)
library(class)
library(e1071)
data<-read.csv("/Users/zhaoyinzhu/Dropbox/NYU/GA 3033 Predictive Analysis/Project/PredictiveAnalysis_EbayAuction/data/SelectedFeatureData.csv")

# Logistic Regression
logfit<-glm(QuantitySold~StartingBidPercent+SellerClosePercent+StartingBid+AvgPrice+AuctionCount
           +AuctionSaleCount+SellerAuctionCount+AuctionMedianPrice,family=binomial,data=data)
cv.logfit<-cv.glm(data,logfit,K=10)
1-cv.logfit$delta
predict(logfit,)
n<-nrow(data)
traindata<-data[1:250000,]
testdata<-data[-(1:250000),]
train<-data[1:250000,-1]
test<-data[-(1:250000),-1]
cl<-data[1:250000,1]
# KNN
knnfit<-knn(train,test,cl,k=9)
mean(knnfit==data[-(1:250000),1])

# Naive Bayes
nbfit<-naiveBayes(QuantitySold~. ,data=traindata)
predict(nbfit,testdata,type="class")
