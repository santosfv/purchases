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