library(tseries)
library("rugarch")
library(zoo)
library(parallel)
library(DEoptim)
library(PerformanceAnalytics)
library("aTSA")

setwd("/Users/fabian/Desktop")

#(a)
ether.ret <- read.csv("Ethereum.csv", header = TRUE)
ether.ret[is.na(ether.ret),]    #No NaN's

#(a1)
plot(ether.ret$Log_Return, type = "l")

par(mfrow=c(1,2))
acf(ether.ret$Log_Return)
pacf(ether.ret$Log_Return)
graphics.off()

adf.test(ether.ret$Log_Return)
kpss.test(ether.ret$Log_Return)
kpss.test(ether.ret$Log_Return, null = "Trend")

#(a2)
min.aic <- Inf

best.arma <- function(input){
  
  print("ooooooooooo  Models  ooooooooooooo")
  for (p in 0:6){
    for (q in 0:6){
      temp.model <- arima(input, c(p, 0, q), method = "ML")
      temp.aic <- temp.model$aic
      
      print(paste0("ARMA(", p, ",", q, ")     ML = ", temp.model$loglik,"     AIC = ", temp.aic))
      if(temp.aic < min.aic){
        opt.p <- p
        opt.q <- q
        min.aic <- temp.aic
        opt.model <- temp.model
      }
    }
  }
  
  print("")
  print("ooooooooooo  Optimal Model  ooooooooooooo")
  print(paste0("ARMA(", opt.p, ",", opt.q, ")     ML = ", opt.model$loglik,"     AIC = ", min.aic))
  return(opt.model)
}

ether.arma <- best.arma(ether.ret$Log_Return)

#(a3)
res <- ether.arma$residuals

par(mfrow = c(1,2))
chart.QQPlot(res, distribution = "norm")
chart.QQPlot(res, distribution = "t", df = 3)
graphics.off()

Box.test(res, type = "Ljung-Box")

#(b)
#(b1)
res2 <- res*res

plot(res2)

par(mfrow = c(1,2))
acf(res2)
pacf(res2)
graphics.off()

#(b2)
min.aic <- Inf

best.garch <- function(input){
  print("ooooooooooo  Models  ooooooooooooo")
  for(p in 0:3){
    for(q in 0:2){
      temp.spec <- ugarchspec(mean.model = list(armaOrder = c(2, 4)), variance.model = list(model="sGARCH",garchOrder=c(p,q)))
      
      tryCatch({
        temp.fit <- ugarchfit(temp.spec, input, solver = "hybrid")
        temp.aic <- infocriteria(temp.fit)[1]
        
        if(temp.aic < min.aic){
          opt.p <- p
          opt.q <- q
          min.aic <- temp.aic
          opt.fit <- temp.fit
        }
      },error=function(e){print("Error")})
      
      print(paste0("ARMA(2,4)     GARCH(", p, ",", q, ")     AIC = ", temp.aic))
    }
  }
  print("")
  print("ooooooooooo  Optimal Model  ooooooooooooo")
  print(paste0("ARMA(2,4)     GARCH(", opt.p, ",", opt.q, ")     AIC = ", min.aic))
  return(opt.fit)
}

ether.garch <- best.garch(ether.ret$Log_Return)

arch.test(ether.arma)

#(b3)
signtest <- signbias(ether.garch)

egarchspec <- ugarchspec(mean.model = list(armaOrder = c(2, 4)), variance.model = list(model="eGARCH",garchOrder=c(3,1)))
egarchfit <- ugarchfit(egarchspec, ether.ret$Log_Return, solver = "hybrid")
infocriteria(egarchfit)

gjrgarchspec <- ugarchspec(mean.model = list(armaOrder = c(2, 4)), variance.model = list(model="gjrGARCH",garchOrder=c(3,1)))
gjrgarchfit <- ugarchfit(gjrgarchspec, ether.ret$Log_Return, solver = "hybrid")
infocriteria(gjrgarchfit)

#(c)
cl<-makeCluster(detectCores()-1)

rollingforecast <- ugarchroll(spec = egarchspec, data = ether.ret$Log_Return, n.ahead = 1, forecast.length = 100,refit.every = 1, 
                            refit.window = "recursive", window.size = 500, solver = "hybrid", calculate.VaR = TRUE, 
                            VaR.alpha = 0.05, keep.coef = TRUE, cluster = cl)

VaRroll <- c(rep(NA, 100))
for (i in 1:100) {
  VaRroll[i] <- rollingforecast@forecast$VaR$`alpha(5%)`[i]
}

ret100 <- ether.ret$Log_Return[977:1076]

plot(ret100, type = "l")
lines(VaRroll, type = "l", col = "red")

#(d)
#(d2)
ind <- c(rep(0, 100))
for (i in 1:100) {
  ind[i] <- as.numeric(ret100[i] < VaRroll[i])
}

Loss <- (c(rep(0.05, 100)) - ind) * (ret100 - VaRroll)
LossOLS <- (ret100 - VaRroll) * (ret100 - VaRroll)
plot(Loss, type = "l")
lines(LossOLS, type = "l", col ="red")

#(d3)
lossfunction <- function(beta, input){
  
  VaR <- rep(0, length(input))
  VaR[1] <-  quantile(input, probs = 0.05)
  e <- rnorm(length(input))
  
  for(i in 2:length(input)){
    VaR[i] <- beta[1] + beta[2] * VaR[i-1] + beta[3] * abs(input[i-1] + e[i])
  }
  
  ind <- c(rep(0, length(input)))
  for (i in 1:length(input)) {
    ind[i] <- as.numeric(input[i] < VaR[i])
  }
  loss <- sum((c(rep(0.05, length(input))) - ind) * (input - VaR))
  
  return(loss)
  
}

lossfunction(beta = c(0,0,0), input = ether.ret$Log_Return)

#(d4)
optModel <- DEoptim(lossfunction, lower = c(-1,-1,-1), upper = c(1,1,1), input = ether.ret$Log_Return)

model <- function(beta, input){
  
  VaR <- rep(0, length(input))
  VaR[1] <-  quantile(input, probs = 0.05)
  e <- rnorm(length(input))
  
  for(i in 2:length(input)){
    VaR[i] <- beta[1] + beta[2] * VaR[i-1] + beta[3] * abs(input[i-1]) + e[i]
  }
  
  return(VaR)
}

VaRt1 <- optModel$optim$bestmem[1] + optModel$optim$bestmem[2] * model(beta = optModel$optim$bestmem, input = ether.ret$Log_Return)[1076] + optModel$optim$bestmem[3] * abs(ether.ret$Log_Return[1076]) + rnorm(1)

#(e4)
LR.test <- function(VaR, return){
  t <- length(VaR)
  hits <- sum(as.integer(return < VaR))
  hit_rate <- mean(as.integer(return < VaR))
  
  LR <- -2*log(0.95^(t-hits) * 0.05^hits) + 2*log((1-hit_rate)^(t-hits) * hit_rate^hits)
  return(LR)
}

chi.quantile <- qchisq(0.95, df=1)

#GARCH
LR.test(VaR = VaRroll, return = ret100)

#Model from (d)
VaRd <- model(beta = optModel$optim$bestmem, input = ether.ret$Log_Return)
LR.test(VaR = VaRd, return = ether.ret$Log_Return)







