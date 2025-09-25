export type Theme = 'light' | 'dark'

export const getTheme = (): Theme => {
  const html = document.documentElement
  const theme = html.getAttribute('data-theme')
  return (theme as Theme) || 'light'
}

export const setTheme = (theme: Theme): void => {
  const html = document.documentElement
  if (theme === 'dark') {
    html.setAttribute('data-theme', 'dark')
  } else {
    html.removeAttribute('data-theme')
  }
  
  localStorage.setItem('theme', theme)
}

export const toggleTheme = (): Theme => {
  const currentTheme = getTheme()
  const newTheme: Theme = currentTheme === 'light' ? 'dark' : 'light'
  setTheme(newTheme)
  return newTheme
}

export const initTheme = (): void => {
  const savedTheme = localStorage.getItem('theme') as Theme
  const systemTheme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  const theme = savedTheme || systemTheme
  setTheme(theme)
}

export const watchSystemTheme = (callback: (theme: Theme) => void): (() => void) => {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  
  const handler = (e: MediaQueryListEvent) => {
    if (!localStorage.getItem('theme')) {
      const theme = e.matches ? 'dark' : 'light'
      setTheme(theme)
      callback(theme)
    }
  }
  
  mediaQuery.addEventListener('change', handler)
  
  return () => {
    mediaQuery.removeEventListener('change', handler)
  }
}