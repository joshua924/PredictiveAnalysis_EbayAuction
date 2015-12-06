# Load data
data<-read.csv("/Users/zhaoyinzhu/Dropbox/NYU/GA 3033 Predictive Analysis/Project/PredictiveAnalysis_EbayAuction/data/SelectedFeatureData.csv")
# Remove invalid values
#for(i in 1:296048){
 # for(j in 2:9){
  #  ifelse(data[i,j]!=0,data[i,j]<-data[i,j],data[i,j]<-NA)
  #}
#}
# Check if log transformation is needed
par(mfrow = c(2, 1))
names<-colnames(data)
for(i in 2:9){
  hist(data[,i],main=paste("Hist of", names[i]),xlab=paste(names[i]))
  hist(log(data[,i]),main=paste("Hist of Log", names[i]),xlab=paste(names[i]))
}
# QuantitySold and SellerClosePercent do not need log transformation
# Log Transformation
#data$LogPricePercent<-log(data$PricePercent)
#data$LogStartingBidPercent<-log(data$StartingBidPercent)
data$LogAvgPrice<-log(data$AvgPrice)
#data$LogAuctionCount<-log(data$AuctionCount)
#data$LogAuctionSaleCount<-log(data$AuctionSaleCount)
#data$LogSellerAuctionCount<-log(data$SellerAuctionCount)
data$LogAuctionMedianPrice<-log(data$AuctionMedianPrice)

## predict sale or not, logistic model
logfit<-glm(QuantitySold~StartingBidPercent+SellerClosePercent+LogAvgPrice+AuctionCount+AuctionSaleCount
            +SellerAuctionCount+LogAuctionMedianPrice,family=binomial,data=data,na.action=na.omit)
summary(logfit)

## function to calculate F measurement
fmeasure<-function(pred,true){
  precision<-sum(pred & true)/sum(pred)
  recall<-sum(pred & true)/sum(true)
  fmeasure<-2*precision*recall/(precision+recall)
  return(list(precision=precision,recall=recall,fmeasure=fmeasure))
}

## predict final price, linear model
lmfit<-lm(PricePercent~StartingBidPercent+SellerClosePercent+LogAvgPrice+AuctionCount+AuctionSaleCount
          +SellerAuctionCount+LogAuctionMedianPrice,data=data)
summary(lmfit)

## Evaluate with test data
testdata<-read.csv("/Users/zhaoyinzhu/Dropbox/NYU/GA 3033 Predictive Analysis/Project/PredictiveAnalysis_EbayAuction/data/TestSet.csv")
testdata$LogAvgPrice<-log(testdata$AvgPrice)
testdata$LogAuctionMedianPrice<-log(testdata$AuctionMedianPrice)

## Evaluate QuantitySold
predlog<-as.numeric(predict(logfit,newdata=testdata)>0.5)
truelog<-testdata$QuantitySold
fmeasure(predlog,truelog)

## Evaluate PricePercent
predlm<-predict(lmfit,newdata=testdata,se.fit=T)
absbias<-mean(abs(predlm$fit-testdata$PricePercent))
sd<-mean(predlm$se.fit)
mean((predlm$fit-testdata$PricePercent)^2)

