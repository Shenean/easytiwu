import {dialog, message, notification} from "../main";
import {LoadingPlugin} from "tdesign-vue-next";
import {i18n} from "../i18n";

const t = i18n.global.t;

export interface MessageOptions {
  content: string;
  duration?: number;
  placement?: 'top' | 'center' | 'bottom';
  closeBtn?: boolean;
}

export interface NotificationOptions {
  title: string;
  content?: string;
  duration?: number;
  placement?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
  closeBtn?: boolean;
}

export interface ConfirmOptions {
  title?: string;
  header?: string;
  body?: string;
  content?: string;
  confirmText?: string;
  cancelText?: string;
  theme?: 'default' | 'info' | 'warning' | 'danger';
}

export interface LoadingInstance {
  hide: () => void;
}

export const useMessage = () => {
  return {
    success: (content: string, duration = 3000) => {
      return message.success(content, duration);
    },
    
    error: (content: string, duration = 3000) => {
      return message.error(content, duration);
    },
    
    warning: (content: string, duration = 3000) => {
      return message.warning(content, duration);
    },
    
    info: (content: string, duration = 3000) => {
      return message.info(content, duration);
    },
    
    message: (content: string, duration = 0) => {
      return message.loading(content, duration);
    },
    
    close: (messageInstance: any) => {
      if (messageInstance && typeof messageInstance.close === 'function') {
        messageInstance.close();
      }
    },
    
    closeAll: () => {
      if (typeof message.closeAll === 'function') {
        message.closeAll();
      }
    },
    
    notify: {
      success: (title: string, content?: string, duration = 4500) => {
        return notification.success({
          title,
          content,
          duration,
        });
      },
      
      error: (title: string, content?: string, duration = 4500) => {
        return notification.error({
          title,
          content,
          duration,
        });
      },
      
      warning: (title: string, content?: string, duration = 4500) => {
        return notification.warning({
          title,
          content,
          duration,
        });
      },
      
      info: (title: string, content?: string, duration = 4500) => {
        return notification.info({
          title,
          content,
          duration,
        });
      },
      
      close: (notificationInstance: any) => {
        if (notificationInstance && typeof notificationInstance.close === 'function') {
          notificationInstance.close();
        }
      },
      
      closeAll: () => {
        if (typeof notification.closeAll === 'function') {
          notification.closeAll();
        }
      }
    },
    
    confirm: (options: string | ConfirmOptions) => {
      const config = typeof options === 'string' 
        ? { title: options }
        : options;
        
      return new Promise<boolean>((resolve) => {
        dialog.confirm({
          header: config.title,
          body: config.content,
          confirmBtn: config.confirmText || t("common.confirm"),
          cancelBtn: config.cancelText || t("common.cancel"),
          theme: config.theme || 'default',
          onConfirm: () => {
            resolve(true);
          },
          onCancel: () => {
            resolve(false);
          },
        });
      });
    },
    
    alert: (title: string, content?: string) => {
      return new Promise<void>((resolve) => {
        dialog.alert({
          header: title,
          body: content,
          confirmBtn: t("common.confirm"),
          onConfirm: () => {
            resolve();
          },
        });
      });
    },
    
    loadingPlugin: {
      show: (text?: string) => {
        return LoadingPlugin({ 
          text: text || t("common.loading"),
          preventScrollThrough: true
        });
      },
      
      hide: (instance?: LoadingInstance) => {
        if (instance && typeof instance.hide === 'function') {
          instance.hide();
        }
      }
    },
    
    notifySuccess: (title: string, content?: string, duration = 4500) => {
      return notification.success({
        title,
        content,
        duration,
      });
    },
    
    notifyError: (title: string, content?: string, duration = 4500) => {
      return notification.error({
        title,
        content,
        duration,
      });
    },
    
    notifyWarning: (title: string, content?: string, duration = 4500) => {
      return notification.warning({
        title,
        content,
        duration,
      });
    },
    
    notifyInfo: (title: string, content?: string, duration = 4500) => {
      return notification.info({
        title,
        content,
        duration,
      });
    },
    
    startLoading: (text?: string) => {
      return LoadingPlugin({ 
        text: text || t("common.loading"),
        preventScrollThrough: true
      });
    },
    
    finishLoading: (instance?: LoadingInstance) => {
      if (instance && typeof instance.hide === 'function') {
        instance.hide();
      }
    },
    
    errorLoading: (instance?: LoadingInstance) => {
      if (instance && typeof instance.hide === 'function') {
        instance.hide();
      }
    },
  };
};

export const messageUtils = {
  success: (content: string, duration = 3000) => message.success(content, duration),
  error: (content: string, duration = 3000) => message.error(content, duration),
  warning: (content: string, duration = 3000) => message.warning(content, duration),
  info: (content: string, duration = 3000) => message.info(content, duration),
  loading: (content: string, duration = 0) => message.loading(content, duration),
};

export const notificationUtils = {
  success: (title: string, content?: string, duration = 4500) => 
    notification.success({ title, content, duration }),
  error: (title: string, content?: string, duration = 4500) => 
    notification.error({ title, content, duration }),
  warning: (title: string, content?: string, duration = 4500) => 
    notification.warning({ title, content, duration }),
  info: (title: string, content?: string, duration = 4500) => 
    notification.info({ title, content, duration }),
};

export const dialogUtils = {
  confirm: (title: string, content?: string) => {
    return new Promise<boolean>((resolve) => {
      dialog.confirm({
        header: title,
        body: content,
        confirmBtn: i18n.global.t("common.confirm"),
        cancelBtn: i18n.global.t("common.cancel"),
        onConfirm: () => resolve(true),
        onCancel: () => resolve(false),
      });
    });
  },
  alert: (title: string, content?: string) => {
    return new Promise<void>((resolve) => {
      dialog.alert({
        header: title,
        body: content,
        confirmBtn: i18n.global.t("common.confirm"),
        onConfirm: () => resolve(),
      });
    });
  }
};

export const loadingUtils = {
  show: (text?: string) => LoadingPlugin({ 
    text: text || i18n.global.t("common.loading"),
    preventScrollThrough: true 
  }),
  hide: (instance?: LoadingInstance) => {
    if (instance && typeof instance.hide === 'function') {
      instance.hide();
    }
  }
};

export { message, notification, dialog, LoadingPlugin as loadingBar };

export default useMessage;
