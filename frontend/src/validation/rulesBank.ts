import type {FormRule, UploadFile} from "tdesign-vue-next";
import {i18n} from "../i18n";

const t = i18n.global.t;

export const bankFormRules: Record<string, FormRule[]> = {
  name: [
    {
      required: true,
      message: t("validation.bankNameRequired"),
      trigger: "blur",
    },
    {
      max: 15,
      message: t("validation.maxLength", { count: 15 }),
      trigger: "change",
    },
  ],
  description: [
    {
      max: 30,
      message: t("validation.maxLength", { count: 30 }),
      trigger: "change",
    },
  ],
  file: [
    {
      required: true,
      message: t("validation.fileRequired"),
      trigger: "change",
      validator: (value: UploadFile[]) => {
        if (!value || value.length === 0) {
          return {
            result: false,
            message: t("validation.fileRequired"),
            type: "error" as const
          };
        }
        return true;
      },
    },
  ],
};
