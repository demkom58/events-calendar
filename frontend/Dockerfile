# Stage 1: Builder
FROM node:23-bookworm AS builder

# Install pnpm globally
RUN npm install -g pnpm

WORKDIR /app

# Copy dependency manifests and lockfile
COPY package.json pnpm-lock.yaml ./

# Install dependencies using pnpm
RUN pnpm install

# Copy all source files
COPY . .

# Build the Vite app (the built files will be placed in /app/dist)
RUN pnpm run build

# Stage 2: Production - Serve static files with Nginx
FROM nginx:stable-alpine

# Copy built assets from the builder stage to Nginx HTML directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Optionally, use a custom Nginx configuration by copying your config file
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
