import type {FormRules, UploadFileInfo} from "naive-ui";
import {i18n} from "../i18n";

const t = i18n.global.t;

export const bankFormRules: FormRules = {
  name: [
    {
      required: true,
      message: () => t("validation.bankNameRequired"),
      trigger: "blur",
    },
    {
      max: 15,
      message: () => t("validation.maxLength", { count: 15 }),
      trigger: "input",
    },
  ],
  description: [
    {
      max: 30,
      message: () => t("validation.maxLength", { count: 30 }),
      trigger: "input",
    },
  ],
  file: [
    {
      required: true,
      message: () => t("validation.fileRequired"),
      trigger: "change",
      validator: (_rule: any, value: UploadFileInfo[]) => {
        if (!value || value.length === 0) {
          return new Error(t("validation.fileRequired"));
        }
        return true;
      },
    },
  ],
};
