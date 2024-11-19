FROM gradle:8.11-jdk23

# Install necessary packages
RUN apt-get update && apt-get install -y \
    apt-utils \	
    git \
    curl \
    sudo

# Create a non-root user
ARG USERNAME=projecthub_devel
ARG USER_UID=1001
ARG USER_GID=$USER_UID

RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && echo "$USERNAME ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Set working directory
WORKDIR /workspace

USER $USERNAME

# Expose development port
EXPOSE 8080