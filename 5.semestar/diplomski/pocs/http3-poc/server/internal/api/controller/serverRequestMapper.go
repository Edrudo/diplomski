package controller

import (
	"http3-server-poc/internal/domain/models"
)

type ServerRequestMapper interface {
	MapImagePartToImagePartDomainModel(imagePart ImagePart) models.ImagePart
}
