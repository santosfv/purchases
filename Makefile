run:
	./mvnw spring-boot:run

clean:
	./mvnw clean

build:
	./mvnw package

test:
	./mvnw test

# Testing commands
request-create:
	curl -v -XPOST  \
		-d '{"description":"Trans description", "transactionDate": "2025-07-10T16:20:00", "amount":12.34}' \
		-H "Content-Type: application/json" \
		http://localhost:8080/api/purchases

convert-unit:
	$(eval ID := $(shell curl -s -XPOST \
		-d '{"description":"desc", "transactionDate": "2025-07-10T16:20:00", "amount":12.34}' \
		-H "Content-Type: application/json" \
		http://localhost:8080/api/purchases | jq -r '.id'))
	@echo "Created purchase with ID: $(ID)"
	curl -v http://localhost:8080/api/purchases/$(ID)/convert/Canada-Dollar