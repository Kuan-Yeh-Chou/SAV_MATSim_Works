library(data.table)
library(dplyr)
library(lubridate)
library(magrittr)
library(ggplot2)
library(lattice)
library(reshape2)
library(tidyr)
library(rgdal)
library(tmap)
library(maptools)
library(sf)
library(formattable)

#Import Travel Data and Town shp
setwd("D:\\TUM\\TS\\Master Thesis\\matsim-example-project\\src\\main\\java\\org\\matsim\\Demand")

TravelData <- read.csv("NationalTravelSurveyData_Weighted.csv",encoding = "UTF-8",header = T, stringsAsFactors=F)
TP_SHP <- st_read(dsn="D:\\TUM\\TS\\Master Thesis\\matsim-example-project\\src\\main\\java\\org\\matsim\\Demand\\SHP", layer="TPMetropolitanDistricts")
County_Town_NameList <- read.csv("County_Town_NameList.csv",header = T, stringsAsFactors=F)

# Assign County and Town name to TravelData
  for(i in 1:nrow(TravelData)){
    for(j in 1:nrow(County_Town_NameList)){
      if(TravelData$OriginTripDistrict[i] == County_Town_NameList$COUNTY_TOWN_ID[j]) {
        TravelData$OriginTripTownName[i] <- County_Town_NameList$COUNTY_TOWN_NAME[j]
      }
      if(TravelData$TripPurposeDistrict[i] == County_Town_NameList$COUNTY_TOWN_ID[j]) {  
        TravelData$DestinationTripTownName[i] <- County_Town_NameList$COUNTY_TOWN_NAME[j] 
      }}} 
rm(i,j)

#Combine TP_SHP County and Town name
TP_SHP$COUNTY_TOWN_NAME <- paste(TP_SHP$COUNTYNAME,substr(TP_SHP$TOWNNAME,1,3),sep = "") #基隆市多一個基

TP_SHP$TOWNID <- as.character(TP_SHP$TOWNID)

  for(i in 1:nrow(TravelData)){
    for(j in 1:nrow(TP_SHP)){
      if(TravelData$OriginTripTownName[i] == TP_SHP$COUNTY_TOWN_NAME[j]) {
        TravelData$origin[i] <- TP_SHP$TOWNID[j]
      }
      if(TravelData$DestinationTripTownName[i] == TP_SHP$COUNTY_TOWN_NAME[j]) {  
        TravelData$destination[i] <- TP_SHP$TOWNID[j] 
      }}} 
rm(i,j)

TravelData$toFromPrefix <- paste(TravelData$OriginTripTownName,TravelData$DestinationTripTownName,sep = "-")

#Extract data
names(TravelData)[1] <- "Person_ID"
TravelData$TotalTravelTime <- TravelData$TravelTime_1 + TravelData$TravelTime_2 + TravelData$TravelTime_3 + TravelData$TravelTime_4
TravelData$OutVehTime <- TravelData$TotalTravelTime - TravelData$InVehTime


TravelData <- as.data.table(select(TravelData,c("Person_ID","Age","TripPurpose","Mode","Mode_1","Mode_2","Mode_3","Mode_4",
                                  "TotalTravelTime","InVehTime","OutVehTime","TravelTime_1","TravelTime_2","TravelTime_3","TravelTime_4",
                                  "origin","destination","OriginTripTownName","DestinationTripTownName","toFromPrefix")))

#check correctness of OD names
pop <- TravelData[,.N,by=c("origin","destination","OriginTripTownName","DestinationTripTownName","toFromPrefix")]
pop2 <- TravelData[,.N,by=c("origin","destination")]
setdiff(pop$N,pop2$N)
names(pop)[6]<-"population"
rm(pop2)

#Sum up the population and mode share 

modeShare <- TravelData[,.N,by=c("origin","destination","OriginTripTownName","DestinationTripTownName","toFromPrefix","Mode")] 
modeShare <- left_join(modeShare,pop)
modeShare$ModeShare <- modeShare$N/modeShare$population
modeShare <- modeShare[order(modeShare$toFromPrefix),]
modeShare$ID <- 1:nrow(modeShare) #Add id to indentify each OD matrix since some modes have same share, R will combine them as same row

OutputData <- as.data.frame(spread(modeShare, Mode, ModeShare))
OutputData[is.na(OutputData)] <- 0
names(OutputData)[c(9:16)] <- c("shareOfCar","shareOfScooter","shareOfTrain", "shareOfTaxi",
                          "shareOfMRT", "shareOfBus", "shareOfBike", "shareOfWalk")
reduceIndex <- NULL
for (i in 1:(nrow(OutputData)-1)) {
  if(OutputData$toFromPrefix[i]==OutputData$toFromPrefix[i+1]){
    OutputData[i+1,c(9:16)] <- OutputData[i,c(9:16)] + OutputData[i+1,c(9:16)]
    reduceIndex <- append(reduceIndex,i)
  }
}
OutputData <- OutputData[-reduceIndex,]
OutputData <- select(OutputData, c("population","origin","destination","toFromPrefix","shareOfCar","shareOfScooter","shareOfTrain", "shareOfTaxi",
                       "shareOfMRT", "shareOfBus", "shareOfBike", "shareOfWalk"))

#check if the number of OD is correct
nrow(OutputData)==nrow(pop)

rm(County_Town_NameList, modeShare, pop,TP_SHP, TravelData, i, reduceIndex)


write.csv(OutputData,"ODMatrix.csv")


