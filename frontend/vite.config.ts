import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import type { ServerResponse, IncomingMessage } from 'http'
import type { ClientRequest } from 'http'
import type { IncomingMessage as ProxyIncomingMessage } from 'http'
import type { ProxyServer, ProxyReqCallback, ProxyResCallback } from 'http-proxy'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        configure: (proxy: ProxyServer) => {
          proxy.on('error', (err: Error, req: IncomingMessage, res: ServerResponse) => {
            console.error('proxy error', err)
          })
          proxy.on('proxyReq', (proxyReq: ClientRequest, req: IncomingMessage, res: ServerResponse) => {
            console.log('Sending Request to Target:', req.method, req.url)
          })
          proxy.on('proxyRes', (proxyRes: ProxyIncomingMessage, req: IncomingMessage, res: ServerResponse) => {
            console.log('Received Response from Target:', proxyRes.statusCode, req.url)
          })
        }
      }
    }
  }
})
