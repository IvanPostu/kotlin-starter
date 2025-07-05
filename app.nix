with import <nixpkgs> {};

mkShell {
  name = "app-shell";

  buildInputs = [ ];

  shellHook = ''
    export APPLICATION_HTTP_PORT=8080
    # echo "APPLICATION_HTTP_PORT set to $APPLICATION_HTTP_PORT"
  '';
}
