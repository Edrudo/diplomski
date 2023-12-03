package router

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

const (
	ImageProcessingRoute = "/processImage"
)

type Controller interface {
	ProcessImagePart(requestContext *gin.Context)
}

// GenerateRoutingHandler creates a new routing handler which routes the requests accordingly
func GenerateRoutingHandler(
	controller Controller,
) (http.Handler, error) {
	gin.SetMode(gin.ReleaseMode)

	// Set up a fresh router with strict slash enabled.
	router := gin.New()
	router.RedirectTrailingSlash = true

	// IMAGE PROCESSING ENDPOINT
	{
		router.GET(ImageProcessingRoute, controller.ProcessImagePart)
	}

	return router, nil
}