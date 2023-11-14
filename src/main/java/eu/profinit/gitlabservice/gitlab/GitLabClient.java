package eu.profinit.gitlabservice.gitlab;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import eu.profinit.gitlabservice.gitlab.dto.GitLabProjectDto;
import eu.profinit.gitlabservice.gitlab.dto.GitLabUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitLabClient {

  private final String GITLAB_USER_URL = "https://gitlab.com/api/v4/users?username=%s";
  private final String GITLAB_PROJECTS_URL = "https://gitlab.com/api/v4/users/%s/projects";

  private final RestTemplate restTemplate;

  public List<GitLabProjectDto> getUserProjects(final String user) {
    final String url = String.format(GITLAB_PROJECTS_URL, user);
    final ResponseEntity<List<GitLabProjectDto>> response =
            restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });

    return response.getBody();
  }

  public GitLabUserDto getUser(final String user) {
    final String url = String.format(GITLAB_USER_URL, user);
    final ResponseEntity<List<GitLabUserDto>> response =
            restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });
    if (response.getBody() != null && !response.getBody().isEmpty()) {
      return response.getBody().get(0);
    }
    return null;
  }
}
