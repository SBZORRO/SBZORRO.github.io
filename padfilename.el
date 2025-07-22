;; -*- lexical-binding: t; -*-

(defun pad-filename (dir)
  "pad log filename seq to 3 width"
  (interactive "DDirectory to apply: \n")

  (dolist (file (directory-files dir t "^[^.].*"))
    (when (file-regular-p file)
      (let* ((filename (file-name-nondirectory file))
              (dir (file-name-directory file))
              (prefix (file-name-sans-extension filename))
              (suffix (file-name-extension filename))
              (last-dot (string-match "\\.\\([0-9]+\\)$" filename)))
        (when (and last-dot
                (< (length (match-string 1 filename)) 3))
          (let* ((orig-suffix (match-string 1 filename))
                  (new-suffix (format "%+3s" orig-suffix))
                  (new-suffix (replace-regexp-in-string " " "0" new-suffix))
                  (new-name (concat (substring filename 0 last-dot) "." new-suffix)))
            (rename-file file (expand-file-name new-name dir))
            (message "Renamed: %s -> %s" filename new-name)))))))
