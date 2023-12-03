package main

import (
	"http3-server-poc/cmd/api/bootstrap"

	"go.uber.org/zap"
)

func main() {
	logger, _ := zap.NewProduction()
	httpApi := bootstrap.Api(logger)

	err := httpApi.ListenAndServe()
	if err != nil {
		panic(err)
	}
}
