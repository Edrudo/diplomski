package services

type ImageStore interface {
	StoreImage(imageHash string, image []byte) error
}
