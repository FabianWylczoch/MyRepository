library(vars)
library(tseries)
library(lubridate)
library(data.table)
library(tidyverse)
library(highfrequency)
library(xts)

setwd("/Users/fabian/Desktop")

#Problem 1

amazon <- fread("AMZN.csv")
amazon$Time <- as_datetime(amazon$Time)

#(a)
#(a1)
amazon$Price <- (amazon$BestAsk + amazon$BestBid) / 2
plot(amazon$Time, amazon$Price, type = "l", xlab = "Time", ylab = "Price")

#(a2)
#Aggregation
amazon1min <- amazon %>%
  filter(Type == 4) %>%
  mutate(Time = floor_date(Time, unit = "minutes")) %>%
  group_by(Time) %>%          #Keeps Time column and adds those stated in summarise
  summarise(MeanPrice = mean(Price), WeightedPrice = weighted.mean(Price, Volume), 
            Volume = sum(Volume), nTrades = n())
#Volume/No. of Trades plot 
ggplot(amazon1min, aes(x=Time)) + 
  geom_line(aes(y=Volume, color="Volume")) +
  geom_line(aes(y=nTrades, color="Number of Trades")) +
  ylab("Volume & #Trades") +
  labs(color="Legend")
#Volume/No. of Trades plot with log10 y-Axis
ggplot(amazon1min, aes(x=Time)) + 
  geom_line(aes(y=Volume, color="Volume")) +
  geom_line(aes(y=nTrades, color="Number of Trades")) +
  scale_y_continuous(trans='log10') +
  ylab("Volume & #Trades") +
  labs(color="Legend")
#Plot of Prices
ggplot(amazon1min, aes(x=Time)) + 
  geom_line(aes(y=MeanPrice, color="Mean Price")) +
  geom_line(aes(y=WeightedPrice, color="Weighted Price")) +
  ylab("Price") +
  labs(color="Legend")


#(a3)
amazonHF <- data.table(DT = amazon$Time, PRICE = amazon$Price)

aggregation <- function(data, delta = 20){
  agg <- aggregatePrice(pData = data, alignBy = "seconds", alignPeriod = delta)
  agg$RETURN <- makeReturns(agg$PRICE)
  return(agg)
}

colors <- rainbow(30)
d = 20
amazon_agg <- aggregation(amazonHF, d)
plot(amazon_agg$DT, amazon_agg$RETURN, type = "l", col = colors[d/20])

while (d <= 580) {
  d = d+20
  amazon_agg <- aggregation(amazonHF, d)
  lines(amazon_agg$DT, amazon_agg$RETURN, type = "l", col = colors[d/20])
}

#(b)
#(b1)
rv <- function(data, d = 20){
  agg <- aggregation(data, d)
  return(sum(agg$RETURN*agg$RETURN))
}

rv30 <- rv(amazonHF, 30)

#b(3)
rq <- function(data, d = 20){
  agg <- aggregation(data, d)
  return((1/3)*sum(agg$RETURN^4))
}

rq30 <- rq(amazonHF, 30)
lc <- rv30 - ((1.96 * sqrt(2 * rq30)) / sqrt(780))
uc <- rv30 + ((1.96 * sqrt(2 * rq30)) / sqrt(780))

#(c)
deltas <- seq(from = 20, to = 900, by = 20)

crv <- list()
for (d in deltas) {
  crv <- append(crv, rv(amazonHF, d))
}

plot(deltas, crv, type = "l", col = "red", ylab = "RV", ylim = c(0, 16e-05))

#(d)
rvac <- function(data, d = 20){
  agg <- aggregation(data, d)
  aggm1 <- shift(agg$RETURN, type = "lead")
  aggp1 <- shift(agg$RETURN, type = "lag")
  agg <- agg$RETURN[c(-1, -length(agg))]
  aggm1 <- aggm1[c(-1, -length(aggm1))]
  aggp1 <- aggp1[c(-1, -length(aggp1))]
  
  sum(agg*agg + aggm1*agg + agg*aggp1)
}

drv <- list()
for (d in deltas) {
  drv <- append(drv, rvac(amazonHF, d))
}

lines(deltas, drv, type = "l", col = "blue")
legend("bottomright", c("RVac", "RVm"), fill = c("blue", "red"))

#(e)
amazonTSRV <- aggregatePrice(amazonHF, alignBy = "seconds", alignPeriod = 1)
#(e1)
s <- function(K = 1){
  samplerets <- data.table()
  names <- c()
  samplervs <- c()
  for (i in 1:K) {
    sampleprices <- c()
    index = i
    while (index <= length(amazonTSRV$PRICE)) {
      sampleprices <- c(sampleprices, amazonTSRV$PRICE[index]) 
      index = index + K
    }
    name <- paste("Sample", i, sep = " ")
    names <- c(names, name)
    rets <- diff(log(sampleprices))
    samplerets <- cbind(samplerets, rets)
    setnames(samplerets, names)
    samplervs <- c(samplervs, sum(rets*rets))
  }
  srv <- mean(samplervs)
  li <- list(samplerets, samplervs, srv)
  return(li)
}

#Die Schleife von 1 bis 2000 zu durchlaufen dauert ewig.
#Die alternative Sequenz sorgt aufgrund des steigenden Abstandes zwischen
#Folgengliedern allerdings für eine recht repräsentative Darstellung.
altseq <- 2^(1:11)
x <- c()
y <- c()
for (k in altseq){
  print(paste("Step", k, sep = " "))
  x <- c(x, k)
  y <- c(y, s(k)[[3]])
}
plot(x, y, type = "l", xlab = "K", ylab = "SRV")

#(e2)
TSRV <- s(100)[[3]] - (233.01/23400)*rv(amazonHF, 1)



#Problem 2
gf <- read.csv("GreenFuture.csv", header = TRUE)

#(a)
#Plot of CCFs
ccf(gf$GreenFuture, gf$SUS_IND, lag = 30)
ccf(gf$SUS_IND, gf$GreenFuture, lag = 30)
#Plot ACFs
acf(gf$GreenFuture)
acf(gf$SUS_IND)
#Plot PACFs
pacf(gf$GreenFuture)
pacf(gf$SUS_IND)

#(b)
#Estimation of Phi matrices
yt1 <- gf$GreenFuture[23:1000]
yt2 <- gf$SUS_IND[23:1000]
ytm11 <- gf$GreenFuture[22:999]
ytm12 <- gf$SUS_IND[22:999]
ytm2stm51 <- gf$GreenFuture[21:998] + gf$GreenFuture[20:997] +
             gf$GreenFuture[19:996] + gf$GreenFuture[18:995]
ytm2stm52 <- gf$SUS_IND[21:998] + gf$SUS_IND[20:997] +
             gf$SUS_IND[19:996] + gf$SUS_IND[18:995]
ytm6stm221 <- gf$GreenFuture[17:994] + gf$GreenFuture[16:993] +
              gf$GreenFuture[15:992] + gf$GreenFuture[14:991] +
              gf$GreenFuture[13:990] + gf$GreenFuture[12:989] +
              gf$GreenFuture[11:988] + gf$GreenFuture[10:987] +
              gf$GreenFuture[9:986] + gf$GreenFuture[8:985] +
              gf$GreenFuture[7:984] + gf$GreenFuture[6:983] +
              gf$GreenFuture[5:982] + gf$GreenFuture[4:981] +
              gf$GreenFuture[3:980] + gf$GreenFuture[2:979] +
              gf$GreenFuture[1:978]
ytm6stm222 <- gf$SUS_IND[17:994] + gf$SUS_IND[16:993] +
              gf$SUS_IND[15:992] + gf$SUS_IND[14:991] +
              gf$SUS_IND[13:990] + gf$SUS_IND[12:989] +
              gf$SUS_IND[11:988] + gf$SUS_IND[10:987] +
              gf$SUS_IND[9:986] + gf$SUS_IND[8:985] +
              gf$SUS_IND[7:984] + gf$SUS_IND[6:983] +
              gf$SUS_IND[5:982] + gf$SUS_IND[4:981] +
              gf$SUS_IND[3:980] + gf$SUS_IND[2:979] +
              gf$SUS_IND[1:978]

fit1 <- lm(yt1 ~ ytm11 + ytm12 + ytm2stm51 + ytm2stm52 + ytm6stm221 + ytm6stm222 -1)
fit2 <- lm(yt2 ~ ytm11 + ytm12 + ytm2stm51 + ytm2stm52 + ytm6stm221 + ytm6stm222 -1)

#Check on stationarity by looking at eigenvalues
phi1 <- matrix(c(0.35, 1.0684, -0.0007, 0.5403), 2, 2, byrow = TRUE)
phi2 <- matrix(c(-0.0218, 0.0078, 0.0001, 0.0152), 2, 2, byrow = TRUE)
phi3 <- matrix(c(-0.0057, 0.0019, 0.0001, 0.0148), 2, 2, byrow = TRUE)

eigen(phi1)
eigen(phi2)
eigen(phi3)
