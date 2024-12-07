# Stage 1: Build the Angular application
FROM node:22 AS build
WORKDIR /app

# Copy package.json and package-lock.json
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the Angular application
RUN npm run build --prod

# Stage 2: Serve the Angular application
FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html

# Expose the default port
EXPOSE 80

# Start Nginx server
CMD ["nginx", "-g", "daemon off;"]