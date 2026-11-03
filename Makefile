CLUSTER ?= three-classics
NAMESPACE ?= three-classics

.PHONY: up down logs test test-backend test-frontend e2e k8s-up k8s-down

## Build and start db, backend and frontend; the app is at http://localhost:8080
up:
	docker compose up --build -d --wait
	@echo "Three Classics is running at http://localhost:8080"

down:
	docker compose down

logs:
	docker compose logs -f

## Backend unit + integration tests (integration tests need Docker for Testcontainers) and Vitest
test: test-backend test-frontend

test-backend:
	cd backend && ./mvnw -B test

test-frontend:
	cd frontend && npm ci && npm test

## Playwright e2e and layout tests against the compose stack, with the dev endpoint enabled
e2e:
	SPRING_PROFILES_ACTIVE=dev docker compose up --build -d --wait
	cd frontend && npm ci && npx playwright install chromium && E2E_BASE_URL=http://localhost:8080 npx playwright test

## Run the same stack on a local kind cluster, then port-forward to http://localhost:8080
k8s-up:
	kind get clusters | grep -qx $(CLUSTER) || kind create cluster --name $(CLUSTER)
	docker build -t three-classics-backend:local backend
	docker build -t three-classics-frontend:local frontend
	kind load docker-image three-classics-backend:local three-classics-frontend:local --name $(CLUSTER)
	kubectl apply -k k8s/
	kubectl -n $(NAMESPACE) rollout status statefulset/postgres --timeout=180s
	kubectl -n $(NAMESPACE) rollout status deployment/backend --timeout=240s
	kubectl -n $(NAMESPACE) rollout status deployment/frontend --timeout=120s
	@echo "Forwarding http://localhost:8080 (Ctrl+C to stop)"
	kubectl -n $(NAMESPACE) port-forward service/frontend 8080:80

k8s-down:
	kind delete cluster --name $(CLUSTER)
