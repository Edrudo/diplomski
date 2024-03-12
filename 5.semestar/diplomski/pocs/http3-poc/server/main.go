package main

import (
	"http3-server-poc/cmd/api/bootstrap"
	"http3-server-poc/cmd/api/config"

	"go.uber.org/zap"
)

func main() {
	err := config.Load()
	if err != nil {
		panic(err)
	}

	logger, _ := zap.NewProduction()
	httpApi := bootstrap.Api(logger)

	err = httpApi.ListenAndServe()
	if err != nil {
		panic(err)
	}
}
