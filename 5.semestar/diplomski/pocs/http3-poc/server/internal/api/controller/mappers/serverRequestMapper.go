package controllermappers

import (
	"http3-server-poc/internal/api/controller"
	"http3-server-poc/internal/domain/models"
)

type ServerRequestMapper struct{}

func NewServerRequestMapper() *ServerRequestMapper {
	return &ServerRequestMapper{}
}

func (m *ServerRequestMapper) MapImagePartToImagePartDomainModel(imagePart controller.ImagePart) models.ImagePart {
	return models.ImagePart{
		ImageHash:  imagePart.ImageHash,
		PartNumber: imagePart.PartNumber,
		TotalParts: imagePart.TotalParts,
		PartData:   imagePart.PartData,
	}
}
