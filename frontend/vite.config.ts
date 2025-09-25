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
        configure: (proxy) => {
          // 错误处理
          proxy.on("error", (err, _req, res) => {
            console.error("Proxy Error:", err);

            if ("writeHead" in res && !res.headersSent) {
              res.writeHead(500, {
                "Content-Type": "application/json",
              });
              res.end(
                JSON.stringify({
                  error: "Proxy Error",
                  message: err.message,
                  code: "PROXY_ERROR",
                })
              );
            }
          });

          // 请求转发前钩子
          proxy.on("proxyReq", (_proxyReq, req, _res) => {
            console.log("→ Forwarding Request:", req.method, req.url);
          });

          // 收到响应后钩子
          proxy.on("proxyRes", (proxyRes, req, _res) => {
            console.log("← Received Response:", proxyRes.statusCode, req.url);
          });
        },
      },
    },
  },
});
