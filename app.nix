let 
  pkgs = import <nixpkgs> { config = { allowUnfree = true; }; };
in
pkgs.mkShell {
  name = "app-shell";

  buildInputs = [
    pkgs.stdenv.cc.cc.lib
    pkgs.python310Packages.tkinter
    pkgs.python310
    pkgs.jdk8
    pkgs.jdk11
    pkgs.jdk17
    pkgs.jdk21
  ];

  LANG = "en_US.UTF-8";
  LC_ALL = "en_US.UTF-8";

  shellHook = ''
    export LD_LIBRARY_PATH="${pkgs.stdenv.cc.cc.lib}/lib:$LD_LIBRARY_PATH"
    export APPLICATION_HTTP_PORT=8080
    # echo "APPLICATION_HTTP_PORT set to $APPLICATION_HTTP_PORT"

    switch_java() {
      if [ "$1" = "8" ]; then
        export JAVA_HOME=${pkgs.jdk8}
        export PATH=${pkgs.jdk8}/bin:$PATH
        echo "Switched to JDK 8"
      elif [ "$1" = "11" ]; then
        export JAVA_HOME=${pkgs.jdk11}
        export PATH=${pkgs.jdk11}/bin:$PATH
        echo "Switched to JDK 11"
      elif [ "$1" = "17" ]; then
        export JAVA_HOME=${pkgs.jdk17}
        export PATH=${pkgs.jdk17}/bin:$PATH
        echo "Switched to JDK 17"
      elif [ "$1" = "21" ]; then
        export JAVA_HOME=${pkgs.jdk21}
        export PATH=${pkgs.jdk21}/bin:$PATH
        echo "Switched to JDK 21"
      else
        echo "Unknown version. Use: switch_java 8, 11, 17 or 21"
      fi
    }

    export -f switch_java

    switch_java 11
  '';
}
