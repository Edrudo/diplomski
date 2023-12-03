package controller

import (
	"encoding/json"
	"io"
	"net/http"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

type Controller struct {
	logger              *zap.Logger
	imageStoringService ImageStoringService
	serverRequestMapper ServerRequestMapper
}

func NewController(imageStoringService ImageStoringService, logger *zap.Logger) *Controller {
	return &Controller{
		logger:              logger,
		imageStoringService: imageStoringService,
	}
}

// ProcessImagePart processes the received image part
func (c *Controller) ProcessImagePart(requestContext *gin.Context) {
	var httpStatusCode int

	// Read the whole body of the request
	body, err := io.ReadAll(requestContext.Request.Body)
	if err != nil {
		c.logger.Error(
			"processing image part, error while reading request body",
		)
		httpStatusCode = http.StatusInternalServerError
		requestContext.Writer.WriteHeader(httpStatusCode)

		return
	}

	// Unmarshal the received body into a DTO struct
	var imagePartDto ImagePart

	err = json.Unmarshal(body, &imagePartDto)
	if err != nil {
		c.logger.Warn(
			"processing image part, error while unmarshalling",
		)
		httpStatusCode = http.StatusBadRequest
		requestContext.Writer.WriteHeader(httpStatusCode)

		return
	}

	// Map the DTO struct into a domain model
	imagePart := c.serverRequestMapper.MapImagePartToImagePartDomainModel(imagePartDto)

	err = c.imageStoringService.StoreImagePart(imagePart)
	if err != nil {
		c.logger.Warn(
			"processing image part, failed to stor image part",
		)
		httpStatusCode = http.StatusInternalServerError
		requestContext.Writer.WriteHeader(httpStatusCode)
	}
}
