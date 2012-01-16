One day I will actually work on this project...
For now, it is a dumping ground for snippets of code and ideas about what to 
do when i get to it....


;; elisp to get the type of a symbol via ensime rpc
 (defun etap()
   (interactive)
   (let ((tinfo (ensime-rpc-inspect-type-at-point)))
     (beginning-of-line)
     (forward-line)
     (insert (append "" tinfo))))

