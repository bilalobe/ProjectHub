        </div>
    </main>
    <footer>
        <div class="footer-content">
            <p>Generated on ${new Date().format('yyyy-MM-dd HH:mm:ss')} - Version ${content.version}</p>
            <p>ProjectHub Documentation</p>
        </div>
    </footer>
    <script>
        // Initialize syntax highlighting
        hljs.highlightAll();
        
        // Initialize Mermaid diagrams
        mermaid.initialize({ startOnLoad: true });
        
        // Handle table of contents
        document.addEventListener('DOMContentLoaded', function() {
            const toc = document.querySelector('.toc');
            if (toc) {
                const tocToggle = document.createElement('button');
                tocToggle.textContent = 'Toggle Table of Contents';
                tocToggle.onclick = function() {
                    toc.classList.toggle('collapsed');
                };
                toc.parentNode.insertBefore(tocToggle, toc);
            }
        });
    </script>
</body>
</html>