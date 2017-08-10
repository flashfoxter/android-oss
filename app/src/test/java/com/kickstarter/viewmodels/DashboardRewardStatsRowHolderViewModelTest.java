package com.kickstarter.viewmodels;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.factories.ProjectFactory;
import com.kickstarter.factories.ProjectStatsEnvelopeFactory;
import com.kickstarter.libs.Environment;
import com.kickstarter.libs.utils.NumberUtils;
import com.kickstarter.models.Project;
import com.kickstarter.services.apiresponses.ProjectStatsEnvelope;

import org.junit.Test;

import rx.observers.TestSubscriber;

public class DashboardRewardStatsRowHolderViewModelTest extends KSRobolectricTestCase {
  private DashboardRewardStatsRowHolderViewModel.ViewModel vm;

  private final TestSubscriber<String> rewardBackerCount = new TestSubscriber<>();
  private final TestSubscriber<String> rewardMinimum = new TestSubscriber<>();
  private final TestSubscriber<String> percentageOfTotalPledged = new TestSubscriber<>();
  private final TestSubscriber<Pair<Project, Float>> projectAndPledgedForReward = new TestSubscriber<>();

  protected void setUpEnvironment(final @NonNull Environment environment) {
    this.vm = new DashboardRewardStatsRowHolderViewModel.ViewModel(environment);
    this.vm.outputs.rewardBackerCount().subscribe(this.rewardBackerCount);
    this.vm.outputs.rewardMinimum().subscribe(this.rewardMinimum);
    this.vm.outputs.percentageOfTotalPledged().subscribe(this.percentageOfTotalPledged);
    this.vm.outputs.projectAndPledgedForReward().subscribe(this.projectAndPledgedForReward);
  }

  @Test
  public void testRewardBackerCount() {
    final ProjectStatsEnvelope.RewardStats rewardStats = ProjectStatsEnvelopeFactory.RewardStatsFactory
      .rewardStats()
      .toBuilder()
      .backersCount(10)
      .build();

    setUpEnvironment(environment());
    this.vm.inputs.projectAndRewardStats(Pair.create(ProjectFactory.project(), rewardStats));
    this.rewardBackerCount.assertValues(NumberUtils.format(10));
  }

  @Test
  public void testRewardMinimum() {
    final ProjectStatsEnvelope.RewardStats rewardStats = ProjectStatsEnvelopeFactory.RewardStatsFactory
      .rewardStats()
      .toBuilder()
      .minimum(5)
      .build();

    setUpEnvironment(environment());
    this.vm.inputs.projectAndRewardStats(Pair.create(ProjectFactory.project(), rewardStats));
    this.rewardMinimum.assertValues(NumberUtils.format(5));
  }

  @Test
  public void testPercentageOfTotalPledged() {
    final Project project = ProjectFactory.project().toBuilder().pledged(100).build();
    final ProjectStatsEnvelope.RewardStats rewardStats = ProjectStatsEnvelopeFactory.RewardStatsFactory
      .rewardStats()
      .toBuilder()
      .pledged(50)
      .build();

    setUpEnvironment(environment());
    this.vm.inputs.projectAndRewardStats(Pair.create(project, rewardStats));
    this.percentageOfTotalPledged.assertValues("(50%)");
  }

  @Test
  public void testProjectAndPledgedForReward() {
    final Project project = ProjectFactory.project().toBuilder().pledged(100).build();
    final ProjectStatsEnvelope.RewardStats rewardStats = ProjectStatsEnvelopeFactory.RewardStatsFactory
      .rewardStats()
      .toBuilder()
      .pledged(50)
      .build();

    final float pledgedFloat = (float) rewardStats.pledged();

    setUpEnvironment(environment());
    this.vm.inputs.projectAndRewardStats(Pair.create(project, rewardStats));
    this.projectAndPledgedForReward.assertValues(Pair.create(project, pledgedFloat));
  }
}