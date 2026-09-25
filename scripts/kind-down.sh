#!/usr/bin/env bash
# Deletes the local kind cluster "bookstore" (and everything in it).
set -euo pipefail
kind delete cluster --name bookstore
