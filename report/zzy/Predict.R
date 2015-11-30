## Predict Final Price
data<-read.csv("/Users/zhaoyinzhu/Dropbox/NYU/GA 3033 Predictive Analysis/Project/PredictiveAnalysis_EbayAuction/data/SelectedFeatureData.csv")

## predict sale or not, logistic model
logfit<-glm(QuantitySold~StartingBidPercent+SellerClosePercent+AvgPrice+AuctionCount+AuctionSaleCount
            +SellerAuctionCount+AuctionMedianPrice,family=binomial,data=data)
pred<-as.numeric(predict(logfit,type="response")>=0.5)
precision <- sum(pred & data$QuantitySold)/sum(pred)
recall <- sum(pred & data$QuantitySold)/sum(data$QuantitySold)
2 * precision * recall / (precision + recall)

## predict final price, linear model
lmfit<-lm(PricePercent~StartingBidPercent+SellerClosePercent+AvgPrice+AuctionCount+AuctionSaleCount
          +SellerAuctionCount+AuctionMedianPrice,data=data)

