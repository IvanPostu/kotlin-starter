with import <nixpkgs> {};

let 
  jdk8 = pkgs.jdk8;
  jdk17 = pkgs.jdk17;
  jdk21 = pkgs.jdk21;
in
mkShell {
  name = "app-shell";

  buildInputs = [
    jdk8
    jdk11
    jdk17
    jdk21
  ];

  shellHook = ''
    export APPLICATION_HTTP_PORT=8080
    # echo "APPLICATION_HTTP_PORT set to $APPLICATION_HTTP_PORT"

    switch_java() {
      if [ "$1" = "8" ]; then
        export JAVA_HOME=${jdk8}
        export PATH=${jdk8}/bin:$PATH
        echo "Switched to JDK 8"
      elif [ "$1" = "11" ]; then
        export JAVA_HOME=${jdk11}
        export PATH=${jdk11}/bin:$PATH
        echo "Switched to JDK 11"
      elif [ "$1" = "17" ]; then
        export JAVA_HOME=${jdk17}
        export PATH=${jdk17}/bin:$PATH
        echo "Switched to JDK 17"
      elif [ "$1" = "21" ]; then
        export JAVA_HOME=${jdk21}
        export PATH=${jdk21}/bin:$PATH
        echo "Switched to JDK 21"
      else
        echo "Unknown version. Use: switch_java 8, 11, 17 or 21"
      fi
    }

    export -f switch_java

    switch_java 11
  '';
}
