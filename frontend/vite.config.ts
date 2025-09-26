// vite.config.ts
import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";
import {resolve} from "path";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import {TDesignResolver} from "unplugin-vue-components/resolvers";

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [
        TDesignResolver({
          library: "vue-next",
        }),
      ],
    }),
    Components({
      resolvers: [
        TDesignResolver({
          library: "vue-next",
        }),
      ],
    }),
  ],
  resolve: {
    alias: {
      "@": resolve(__dirname, "src"),
    },
  },
  server: {
    host: "0.0.0.0",
    port: 3000,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path,
        configure: (proxy) => {
          proxy.on("error", (err, req, res) => {
            const timestamp = new Date().toISOString();
            console.error(`[${timestamp}] Proxy Error:`, {
              url: req.url,
              method: req.method,
              error: err.message,
            });

            if ("writeHead" in res && !res.headersSent) {
              res.writeHead(500, {
                "Content-Type": "application/json",
                "Access-Control-Allow-Origin": "*",
              });
              res.end(
                JSON.stringify({
                  success: false,
                  error: "代理服务器错误",
                  message: "无法连接到后端服务",
                  code: "PROXY_ERROR",
                  timestamp,
                })
              );
            }
          });

          proxy.on("proxyReq", (proxyReq, req, _res) => {
            const timestamp = new Date().toISOString();
            console.log(`[${timestamp}] → 转发请求:`, {
              method: req.method,
              url: req.url,
              headers: proxyReq.getHeaders(),
            });
          });

          proxy.on("proxyRes", (proxyRes, req, _res) => {
            const timestamp = new Date().toISOString();
            console.log(`[${timestamp}] ← 收到响应:`, {
              status: proxyRes.statusCode,
              url: req.url,
              method: req.method,
            });
          });
        },
      },
    },
  },
});
