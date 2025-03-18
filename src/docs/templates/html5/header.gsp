<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ProjectHub Documentation - ${content.title}</title>
    <link rel="stylesheet" href="css/projecthub.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/default.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/mermaid/10.6.1/mermaid.min.js"></script>
</head>
<body>
    <header>
        <nav class="top-nav">
            <div class="nav-logo">ProjectHub</div>
            <div class="nav-links">
                <a href="index.html">Home</a>
                <a href="architecture.html">Architecture</a>
                <a href="development.html">Development</a>
                <a href="operations.html">Operations</a>
            </div>
        </nav>
    </header>
    <main>
        <div class="sidebar">
            <% if (content.toc) { %>
                ${content.toc}
            <% } %>
        </div>
        <div class="content">
            <h1>${content.title}</h1>