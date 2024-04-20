# Use the latest Ubuntu base image
FROM ubuntu:20.04

# Avoid prompts from apt-get
ARG DEBIAN_FRONTEND=noninteractive

# Update apt and install necessary packages
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    software-properties-common \
    perl \
    git \
    ca-certificates \
    lsb-release \
    gnupg \
    npm \
    && rm -rf /var/lib/apt/lists/*

# Install PostgreSQL Client 16
RUN apt install curl ca-certificates
RUN install -d /usr/share/postgresql-common/pgdg
RUN curl -o /usr/share/postgresql-common/pgdg/apt.postgresql.org.asc --fail https://www.postgresql.org/media/keys/ACCC4CF8.asc
RUN sh -c 'echo "deb [signed-by=/usr/share/postgresql-common/pgdg/apt.postgresql.org.asc] https://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
RUN apt-get update
RUN apt-get install -y postgresql-client-16

# Install NodeJS
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
RUN apt-get install -y nodejs

# Add Docker
# Install Docker CLI
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - && \
    echo "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce-cli

# Install TaskFile
RUN npm install --global --unsafe-perm @go-task/cli

# Install Perl XML::XPath module for XPath support
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
RUN cpan XML::XPath

# Install OpenJDK 21
RUN add-apt-repository ppa:openjdk-r/ppa && \
    apt-get update && \
    apt-get install -y openjdk-21-jdk && \
    rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME environment variable
ENV JAVA_HOME /usr/lib/jvm/java-21-openjdk-amd64
ENV PATH $PATH:$JAVA_HOME/bin

# Expose any ports needed (optional, depends on your application)
EXPOSE 8080

# Set default work directory and default command
WORKDIR /app
CMD ["bash"]
