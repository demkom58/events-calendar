# Event calendar

## Launch instructions

1. Clone the repository
2. Run MySQL docker container
    ```bash
    docker compose up
    ```
3. Run the backend:
   - `cd backend`
   - `.\mvnw spring-boot:run` (Windows)
   - `./mvnw spring-boot:run` (Linux)

4. Run the frontend:
   - `cd frontend`
   - `npm install` or `pnpm i`
   - `npm run prod` or `pnpm run prod`
5. Open the browser and go to http://localhost:4173/ for frontend;
6. Open the browser and go to http://localhost:8080/swagger-ui/index.html for backend.