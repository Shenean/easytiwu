import {dialog, loadingBar, message, notification} from "../main";
import {i18n} from "../i18n";

const t = i18n.global.t;

export const useMessage = () => {
  return {
    success: (content: string, duration = 3000) => {
      message.success(content, { duration });
    },
    error: (content: string, duration = 3000) => {
      message.error(content, { duration });
    },
    warning: (content: string, duration = 3000) => {
      message.warning(content, { duration });
    },
    info: (content: string, duration = 3000) => {
      message.info(content, { duration });
    },
    loading: (content: string, duration = 0) => {
      return message.loading(content, { duration });
    },
    notifySuccess: (title: string, content?: string, duration = 4500) => {
      notification.success({
        title,
        content,
        duration,
      });
    },

    notifyError: (title: string, content?: string, duration = 4500) => {
      notification.error({
        title,
        content,
        duration,
      });
    },
    notifyWarning: (title: string, content?: string, duration = 4500) => {
      notification.warning({
        title,
        content,
        duration,
      });
    },
    notifyInfo: (title: string, content?: string, duration = 4500) => {
      notification.info({
        title,
        content,
        duration,
      });
    },
    confirm: (title: string, content?: string) => {
      return new Promise<boolean>((resolve) => {
        dialog.warning({
          title,
          content,
          positiveText: t("common.confirm"),
          negativeText: t("common.cancel"),
          onPositiveClick: () => {
            resolve(true);
          },
          onNegativeClick: () => {
            resolve(false);
          },
        });
      });
    },
    startLoading: () => {
      loadingBar.start();
    },
    finishLoading: () => {
      loadingBar.finish();
    },
    errorLoading: () => {
      loadingBar.error();
    },
  };
};

export { message, notification, dialog, loadingBar };
