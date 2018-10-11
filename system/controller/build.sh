#!/usr/bin/env bash

export CGO_ENABLED=0

go build -o vick-controller -x ./cmd/controller

docker build -t sinthuja/vick-controller .

#docker push mirage20/vick-controller
